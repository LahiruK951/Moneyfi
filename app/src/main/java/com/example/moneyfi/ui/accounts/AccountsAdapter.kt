package com.example.moneyfi.ui.accounts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyfi.data.models.Account
import com.example.moneyfi.databinding.ItemAccountBinding
import java.text.NumberFormat

class AccountsAdapter(
    private val onEditClick: (Account) -> Unit,
    private val onDeleteClick: (Account) -> Unit
) : ListAdapter<Account, AccountsAdapter.AccountViewHolder>(AccountDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        return AccountViewHolder(
            ItemAccountBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AccountViewHolder(
        private val binding: ItemAccountBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(account: Account) {
            binding.apply {
                textAccountName.text = account.name
                textBalance.text = NumberFormat.getCurrencyInstance().format(account.balance)

                buttonEdit.setOnClickListener { onEditClick(account) }
                buttonDelete.setOnClickListener { onDeleteClick(account) }
            }
        }
    }

    private class AccountDiffCallback : DiffUtil.ItemCallback<Account>() {
        override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
            return oldItem == newItem
        }
    }
}