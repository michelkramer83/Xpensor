package com.jxareas.xpensor.ui.transactions.actions.category

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jxareas.xpensor.databinding.BottomSheetSelectCategoryBinding
import com.jxareas.xpensor.ui.main.MainActivityViewModel
import com.jxareas.xpensor.ui.transactions.actions.category.adapter.CategoryAdapter
import com.jxareas.xpensor.ui.transactions.actions.category.event.SelectCategoryEvent
import com.jxareas.xpensor.utils.DateUtils.toAmountFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SelectCategoryBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSelectCategoryBinding? = null
    private val binding: BottomSheetSelectCategoryBinding
        get() = _binding!!

    private val viewModel: SelectCategoryViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    @Inject
    internal lateinit var categoryAdapter: CategoryAdapter

    private val args by navArgs<SelectCategoryBottomSheetArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetSelectCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupRecyclerView()
        setupCollectors()
        setupEventCollector()
    }

    private fun setupEventCollector() {
        lifecycleScope.launchWhenStarted {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is SelectCategoryEvent.SelectCategory -> {
                        val direction =
                            SelectCategoryBottomSheetDirections
                                .actionSelectCategoryBottomSheetToAddTransactionBottomSheet(event.account,
                                    event.category, args.amount)
                        findNavController().navigate(direction)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() = binding.recyclerViewCategories.run {
        adapter = categoryAdapter
        categoryAdapter.setOnClickListener(CategoryAdapter.OnClickListener { category ->
            viewModel.selectCategoryClick(args.selectedAccount, category)
        })
    }

    private fun setupCollectors() {
        lifecycleScope.launchWhenStarted {
            viewModel.categories.collectLatest { newCategories ->
                categoryAdapter.submitList(newCategories)
            }
        }
    }

    private fun setupView() {
        val account = args.selectedAccount
        binding.run {
            accountName.text = account.name
            accountAmount.text = account.amount.toAmountFormat(withMinus = false)
            accountCurrency.text = activityViewModel.getCurrency()
            actionsContainer.setBackgroundColor(Color.parseColor(account.color))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}