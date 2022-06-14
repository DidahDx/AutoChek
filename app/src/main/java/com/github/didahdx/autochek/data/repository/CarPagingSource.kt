package com.github.didahdx.autochek.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.didahdx.autochek.data.remote.api.CarApiServices
import com.github.didahdx.autochek.data.remote.dto.CarDetails
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Daniel Didah on 6/14/22
 */
private const val CAR_PAGE_INDEX = 1
const val NETWORK_PAGE_SIZE = 30

@Singleton
class CarPagingSource @Inject constructor(
    private val carApiService: CarApiServices
) : PagingSource<Int, CarDetails>() {
    override fun getRefreshKey(state: PagingState<Int, CarDetails>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CarDetails> {
        val position = params.key ?: CAR_PAGE_INDEX

        return try {
            val response = carApiService.getCarList(position)
            val repos = response.result
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                position + 1
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position == CAR_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }

    }
}