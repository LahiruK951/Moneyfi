package com.example.moneyfi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneyfi.MoneyfiApplication
import com.example.moneyfi.databinding.FragmentHomeBinding
import com.example.moneyfi.ui.transaction.TransactionAdapter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory(
            (requireActivity().application as MoneyfiApplication).transactionRepository
        )
    }

    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupDateRangeCard()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.recyclerTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupDateRangeCard() {
        binding.cardDateRange.setOnClickListener {
            // TODO: Show date range picker dialog
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactions.collectLatest { transactions ->
                transactionAdapter.submitList(transactions)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.startDate.collectLatest { startDate ->
                viewModel.endDate.value.let { endDate ->
                    updateDateRangeText(startDate, endDate)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalIncome.collectLatest { income ->
                updateIncomeText(income)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalExpense.collectLatest { expense ->
                updateExpenseText(expense)
                updateBalanceText(viewModel.totalIncome.value - expense)
            }
        }
    }

    private fun updateDateRangeText(startDate: Date, endDate: Date) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        binding.textDateRange.text = "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"
    }

    private fun updateIncomeText(income: Double) {
        binding.textIncome.text = "Income: ${formatCurrency(income)}"
    }

    private fun updateExpenseText(expense: Double) {
        binding.textExpense.text = "Expense: ${formatCurrency(expense)}"
    }

    private fun updateBalanceText(balance: Double) {
        binding.textBalance.text = "Balance: ${formatCurrency(balance)}"
    }

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance().format(amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}