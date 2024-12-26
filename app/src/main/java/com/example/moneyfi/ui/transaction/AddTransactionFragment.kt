package com.example.moneyfi.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.moneyfi.R
import com.example.moneyfi.data.AppDatabase
import com.example.moneyfi.data.models.TransactionType
import com.example.moneyfi.data.repository.AccountRepository
import com.example.moneyfi.data.repository.CategoryRepository
import com.example.moneyfi.data.repository.TransactionRepository
import com.example.moneyfi.databinding.FragmentAddTransactionBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionFragment : Fragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels {
        AddTransactionViewModel.Factory(
            TransactionRepository(AppDatabase.getDatabase(requireContext()).transactionDao()),
            AccountRepository(AppDatabase.getDatabase(requireContext()).accountDao()),
            CategoryRepository(AppDatabase.getDatabase(requireContext()).categoryDao())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTransactionTypeSpinner()
        setupDatePicker()
        setupAccountSpinners()
        setupCategorySpinner()
        setupSaveButton()
        observeViewModel()
    }

    private fun setupTransactionTypeSpinner() {
        val types = TransactionType.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter

        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedType = TransactionType.values()[position]
                viewModel.onTransactionTypeSelected(selectedType)

                // Show/hide relevant views based on transaction type
                binding.layoutToAccount.visibility =
                    if (selectedType == TransactionType.TRANSFER) View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupDatePicker() {
        binding.editDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                viewModel.onDateSelected(Date(selection))
                binding.editDate.setText(
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(Date(selection))
                )
            }

            datePicker.show(parentFragmentManager, "date_picker")
        }
    }

    private fun setupAccountSpinners() {
        viewModel.accounts.observe(viewLifecycleOwner) { accounts ->
            val accountNames = accounts.map { it.name }

            // Setup from account spinner
            val fromAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accountNames)
            fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerAccount.adapter = fromAdapter

            // Setup to account spinner (for transfers)
            val toAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accountNames)
            toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerToAccount.adapter = toAdapter
        }
    }

    private fun setupCategorySpinner() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            val amount = binding.editAmount.text.toString().toDoubleOrNull()
            if (amount == null) {
                Snackbar.make(binding.root, "Please enter a valid amount", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val description = binding.editDescription.text.toString()
            if (description.isEmpty()) {
                Snackbar.make(binding.root, "Please enter a description", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.saveTransaction(
                amount = amount,
                description = description,
                fromAccountPosition = binding.spinnerAccount.selectedItemPosition,
                toAccountPosition = binding.spinnerToAccount.selectedItemPosition,
                categoryPosition = binding.spinnerCategory.selectedItemPosition
            )
        }
    }

    private fun observeViewModel() {
        viewModel.saveComplete.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                findNavController().navigateUp()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}