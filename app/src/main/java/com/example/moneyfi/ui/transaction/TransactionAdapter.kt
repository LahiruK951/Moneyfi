package com.example.moneyfi.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyfi.data.models.Transaction
import com.example.moneyfi.data.models.TransactionType
import com.example.moneyfi.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(
    TransactionDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.apply {
                textDescription.text = transaction.description
                textAmount.text = formatAmount(transaction)
                textDate.text = formatDate(transaction)

                // Set text color based on transaction type
                textAmount.setTextColor(
                    when (transaction.type) {
                        TransactionType.INCOME -> android.graphics.Color.GREEN
                        TransactionType.EXPENSE -> android.graphics.Color.RED
                        TransactionType.TRANSFER -> android.graphics.Color.BLUE
                    }
                )
            }
        }

        private fun formatAmount(transaction: Transaction): String {
            val prefix = when (transaction.type) {
                TransactionType.INCOME -> "+"
                TransactionType.EXPENSE -> "-"
                TransactionType.TRANSFER -> "→"
            }
            return prefix + NumberFormat.getCurrencyInstance().format(transaction.amount)
        }

        private fun formatDate(transaction: Transaction): String {
            return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(transaction.date)
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}