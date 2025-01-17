package com.jxareas.xpensor.ui.transactions.actions.add

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jxareas.xpensor.R
import com.jxareas.xpensor.databinding.BottomSheetAddTransactionBinding
import com.jxareas.xpensor.domain.model.Transaction
import com.jxareas.xpensor.ui.transactions.actions.add.event.AddTransactionEvent
import com.jxareas.xpensor.ui.transactions.actions.add.state.AddTransactionState
import com.jxareas.xpensor.utils.DateUtils.toAmountFormat
import com.jxareas.xpensor.utils.setIcon
import com.jxareas.xpensor.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddTransactionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddTransactionBinding? = null
    private val binding: BottomSheetAddTransactionBinding
        get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels()
    private val args by navArgs<AddTransactionBottomSheetArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
        setupCollectors()
        setupEventCollector()
    }

    private fun setupCollectors() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.transactionState.collectLatest { state ->
                when (state) {
                    is AddTransactionState.ValidTransaction -> navigateBackToTransactionFragment()
                    is AddTransactionState.InvalidTransaction -> showInvalidTransactionSnackbar()

                }
            }
        }
    }

    private fun setupListeners() = binding.run {
        buttonApplyChanges.setOnClickListener { viewModel.onApplyChanges() }
    }

    private fun setupEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is AddTransactionEvent.CreateNewTransaction -> {
                        val account = args.selectedAccount
                        val category = args.selectedCategory
                        val amount =
                            binding.textInputLayoutExpense.editText?.text.toString()
                                .toDoubleOrNull()
                        if (amount == null || amount <= 0)
                            showInvalidInputSnackbar()
                        else {
                            val note =
                                binding.textInputLayoutDescription.editText?.text.toString()

                            val transaction = Transaction(
                                note = note,
                                amount = amount,
                                accountId = account.id,
                                categoryId = category.id
                            )

                            viewModel.onAddTransaction(transaction)
                        }
                    }
                }
            }
        }
    }

    private fun showInvalidTransactionSnackbar() =
        showSnackbar(errorMessage = getString(R.string.not_enough_funds))

    private fun showInvalidInputSnackbar() =
        showSnackbar(errorMessage = getString(R.string.enter_expense_error))

    private fun navigateBackToTransactionFragment() {
        val direction =
            AddTransactionBottomSheetDirections
                .actionAddTransactionBottomSheetToTransactionsFragment()
        findNavController().navigate(direction)
    }

    private fun setupView() = binding.run {
        accountName.text = args.selectedAccount.name
        categoryName.text = args.selectedCategory.name
        categoryIcon.setIcon(args.selectedCategory.icon)

        if (args.amount != 0f)
            textInputLayoutExpense.editText?.setText(
                args.amount.toDouble().toAmountFormat(withMinus = false)
            )

        accountBackground.setBackgroundColor(Color.parseColor(args.selectedAccount.color))
        categoryBackground.setBackgroundColor(Color.parseColor(args.selectedCategory.iconColor))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}