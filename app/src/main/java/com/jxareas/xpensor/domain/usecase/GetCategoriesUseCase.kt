package com.jxareas.xpensor.domain.usecase

import com.jxareas.xpensor.data.local.views.CategoryView
import com.jxareas.xpensor.domain.model.Account
import com.jxareas.xpensor.domain.repository.CategoryRepository
import com.jxareas.xpensor.utils.DateRange
import com.jxareas.xpensor.utils.DateUtils
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {

    operator fun invoke(dateRange: DateRange, account: Account?):
            Flow<List<CategoryView>> {

        val minDate = dateRange.first ?: DateUtils.DEFAULT_LOCAL_DATE
        val maxDate = dateRange.second ?: DateUtils.getCurrentLocalDate()

        return if (account == null)
            repository.getCategoryViews(minDate, maxDate)
        else repository.getCategoryViewsFromAccount(minDate, maxDate, account.id)

    }


}