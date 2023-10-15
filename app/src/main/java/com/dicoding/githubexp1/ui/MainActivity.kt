package com.dicoding.githubexp1.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubexp1.R
import com.dicoding.githubexp1.adapter.UserAdapter
import com.dicoding.githubexp1.api.ApiConfig
import com.dicoding.githubexp1.databinding.ActivityMainBinding
import com.dicoding.githubexp1.model.ItemsItem
import com.dicoding.githubexp1.model.ResponseSearch
import com.dicoding.githubexp1.setting.SettingPreferences
import com.dicoding.githubexp1.setting.ViewModelFactory
import com.dicoding.githubexp1.viewmodel.MainViewModel
import com.dicoding.githubexp1.viewmodel.SettingViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    private lateinit var viewModelSetting: SettingViewModel

    private val viewModel: MainViewModel by viewModels()
    private val adapter = UserAdapter()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (viewModel.getSearchQuery().isNullOrBlank()) {
            viewModel.setSearchQuery("Arif")
        }

        getRandomGitHubUsers()
        darkModeCheck()
        showViewModel()
        showRecyclerView()
        setupSearchView()

        val searchView = binding.searchView
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        // Mengatur warna teks pada EditText di dalam SearchView
        searchEditText.setTextColor(ContextCompat.getColor(this, android.R.color.black))

        viewModel.getIsLoading.observe(this, this::showLoading)

        binding.fabAdd.setOnClickListener {
            val i = Intent(this, FavoriteActivity::class.java)
            startActivity(i)
        }
        val fabSetting = findViewById<FloatingActionButton>(R.id.setting)
        fabSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

    }

    private fun getRandomGitHubUsers() {

        val apiService = ApiConfig.getApiService()
        val call = apiService.search("Arif")

        call.enqueue(object : Callback<ResponseSearch> {
            override fun onResponse(call: Call<ResponseSearch>, response: Response<ResponseSearch>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val searchResults = response.body()!!.items
                    updateRecyclerView(searchResults)
                }
            }

            override fun onFailure(call: Call<ResponseSearch>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun updateRecyclerView(usersList: List<ItemsItem>) {
        Log.d("GitHubUsers", "Total Users: ${usersList.size}")
        val arrayListUsers = ArrayList<ItemsItem>(usersList)
        adapter.setData(arrayListUsers)
    }


    private fun darkModeCheck() {
        val pref = SettingPreferences.getInstance(dataStore)
        viewModelSetting =
            ViewModelProvider(this, ViewModelFactory(pref))[SettingViewModel::class.java]

        viewModelSetting.getThemeSettings().observe(this@MainActivity) { isDarkModeActive ->
            if (isDarkModeActive) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun showViewModel() {
        viewModel.getSearchList.observe(this) { searchList ->
            if (searchList.size != 0) {
                binding.rvUser.visibility = View.VISIBLE
                adapter.setData(searchList)
            } else {
                binding.rvUser.visibility = View.GONE
                Toast.makeText(this, " User Not Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRecyclerView() {
        binding.rvUser.layoutManager =
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(this, 2)
            } else {
                LinearLayoutManager(this)
            }

        binding.rvUser.setHasFixedSize(true)
        binding.rvUser.adapter = adapter

        adapter.setOnItemClickCallback { data -> selectedUser(data) }
    }

    private fun selectedUser(user: ItemsItem) {
        Toast.makeText(this, "You choose ${user.login}", Toast.LENGTH_SHORT).show()

        val i = Intent(this, UserDetailActivity::class.java)
        i.putExtra(UserDetailActivity.EXTRA_USER, user.login)
        startActivity(i)
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        val linearLayout = binding.bgSearch // Ganti dengan ID LinearLayout Anda

        // Memantulkan event saat pengguna menekan tombol "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    viewModel.searchUser(query!!)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle changes in the search query text if needed
                // This method is called when the query text is changed (e.g., real-time search)
                return true
            }
        })

        // Menanggapi peristiwa ketika pengguna menekan tombol "Enter" di keyboard
        searchView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = searchView.query.toString()
                if (!query.isNullOrBlank()) {
                    viewModel.searchUser(query)
                }
                searchView.clearFocus()
                true
            } else {
                false
            }
        }

        // Menanggapi klik pada elemen dengan ID searchLinearLayout (LinearLayout)
        linearLayout.setOnClickListener {
            searchView.isIconified = false // Membuka SearchView
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting) {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}