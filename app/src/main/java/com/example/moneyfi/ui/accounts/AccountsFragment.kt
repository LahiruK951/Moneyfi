package com.example.moneyfi.ui.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyfi.R
import com.example.moneyfi.data.AppDatabase
import com.example.moneyfi.data.models.Account
import com.example.moneyfi.data.repository.AccountRepository
import com.example.moneyfi.databinding.FragmentAccountsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountsFragment : Fragment() {
    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountsViewModel by viewModels {
        AccountsViewModel.Factory(
            AccountRepository(AppDatabase.getDatabase(requireContext()).accountDao())
        )
    }

    private lateinit var accountsAdapter: AccountsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupAddAccountButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        accountsAdapter = AccountsAdapter(
            onEditClick = { account ->
                showEditAccountDialog(account)
            },
            onDeleteClick = { account ->
                showDeleteAccountDialog(account)
            }
        )

        binding.recyclerAccounts.apply {
            adapter = accountsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupAddAccountButton() {
        binding.fabAddAccount.setOnClickListener {
            showAddAccountDialog()
        }
    }

    private fun showAddAccountDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.edit_account_name)
        val balanceInput = dialogView.findViewById<TextInputEditText>(R.id.edit_initial_balance)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Account")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString()
                val balance = balanceInput.text.toString().toDoubleOrNull() ?: 0.0
                viewModel.addAccount(name, balance)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditAccountDialog(account: Account) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.edit_account_name)
        val balanceInput = dialogView.findViewById<TextInputEditText>(R.id.edit_initial_balance)

        nameInput.setText(account.name)
        balanceInput.setText(account.balance.toString())

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Account")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString()
                val balance = balanceInput.text.toString().toDoubleOrNull() ?: 0.0
                viewModel.updateAccount(account.copy(name = name, balance = balance))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteAccountDialog(account: Account) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete this account? All associated transactions will also be deleted.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteAccount(account)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.accounts.collectLatest { accounts ->
                accountsAdapter.submitList(accounts)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}