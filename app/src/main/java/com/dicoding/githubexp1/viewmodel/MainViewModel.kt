package com.dicoding.githubexp1.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubexp1.api.ApiConfig
import com.dicoding.githubexp1.model.ItemsItem
import com.dicoding.githubexp1.model.GithubResponseDetailUser
import com.dicoding.githubexp1.model.GithubResponseFollow
import com.dicoding.githubexp1.model.ResponseSearch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val searchList = MutableLiveData<ArrayList<ItemsItem>>()
    val getSearchList: LiveData<ArrayList<ItemsItem>> = searchList

    private val userDetail = MutableLiveData<GithubResponseDetailUser>()
    val getUserDetail: LiveData<GithubResponseDetailUser> = userDetail

    private val followers = MutableLiveData<ArrayList<GithubResponseFollow>>()
    val getFollowers: LiveData<ArrayList<GithubResponseFollow>> = followers

    private val following = MutableLiveData<ArrayList<GithubResponseFollow>>()
    val getFollowing: LiveData<ArrayList<GithubResponseFollow>> = following

    private val isLoading = MutableLiveData<Boolean>()
    val getIsLoading: LiveData<Boolean> = isLoading

    private val searchQuery = MutableLiveData<String>()

    private val searchResults = MutableLiveData<List<ItemsItem>>()
    val getSearchResults: LiveData<List<ItemsItem>> = searchResults

    fun setSearchResults(results: List<ItemsItem>) {
        searchResults.value = results
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun getSearchQuery(): String? {
        return searchQuery.value
    }

    fun searchUser(username: String) {
        try {
            isLoading.value = true
            val client = ApiConfig.getApiService().search(username)
            client.enqueue(object : Callback<ResponseSearch> {
                override fun onResponse(
                    call: Call<ResponseSearch>,
                    response: Response<ResponseSearch>
                ) {
                    isLoading.value = false
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        searchList.value = ArrayList(responseBody.items)
                    }
                }

                override fun onFailure(call: Call<ResponseSearch>, t: Throwable) {
                    isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        } catch (e: Exception) {
            Log.d("Token e", e.toString())
        }
    }

    fun detailUser(username: String) {
        try {
            isLoading.value = true
            val client = ApiConfig.getApiService().detailUser(username)
            client.enqueue(object : Callback<GithubResponseDetailUser> {
                override fun onResponse(
                    call: Call<GithubResponseDetailUser>,
                    response: Response<GithubResponseDetailUser>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        userDetail.value = response.body()
                    }
                }

                override fun onFailure(call: Call<GithubResponseDetailUser>, t: Throwable) {
                    isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        } catch (e: Exception) {
            Log.d("Token e", e.toString())
        }
    }

    fun followers(username: String) {
        try {
            isLoading.value = true
            val client = ApiConfig.getApiService().followers(username)
            client.enqueue(object : Callback<ArrayList<GithubResponseFollow>> {
                override fun onResponse(
                    call: Call<ArrayList<GithubResponseFollow>>,
                    response: Response<ArrayList<GithubResponseFollow>>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        followers.value = response.body()
                    }
                }

                override fun onFailure(call: Call<ArrayList<GithubResponseFollow>>, t: Throwable) {
                    isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        } catch (e: Exception) {
            Log.d("Token e", e.toString())
        }
    }

    fun following(username: String) {
        try {
            isLoading.value = true
            val client = ApiConfig.getApiService().following(username)
            client.enqueue(object : Callback<ArrayList<GithubResponseFollow>> {
                override fun onResponse(
                    call: Call<ArrayList<GithubResponseFollow>>,
                    response: Response<ArrayList<GithubResponseFollow>>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful && response.body() != null) {
                        following.value = response.body()
                    }
                }

                override fun onFailure(call: Call<ArrayList<GithubResponseFollow>>, t: Throwable) {
                    isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }
            })
        } catch (e: Exception) {
            Log.d("Token e", e.toString())
        }
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}