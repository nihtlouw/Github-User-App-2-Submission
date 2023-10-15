package com.dicoding.githubexp1.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubexp1.R
import com.dicoding.githubexp1.adapter.FavoriteAdapter
import com.dicoding.githubexp1.databinding.ActivityFavoriteBinding
import com.dicoding.githubexp1.database.FavoriteHelper
import com.dicoding.githubexp1.model.GithubResponseDetailUser

class FavoriteActivity : AppCompatActivity() {

    private val adapter = FavoriteAdapter()
    private lateinit var helper: FavoriteHelper
    private var listFavorite: ArrayList<GithubResponseDetailUser> = ArrayList()

    private lateinit var binding: ActivityFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.favorite)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        helper = FavoriteHelper.getInstance(applicationContext)
        helper.open()

        showRecyclerView()
        getDataFavorite()
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

    private fun selectedUser(user: GithubResponseDetailUser) {
        Toast.makeText(this, "You choose ${user.login}", Toast.LENGTH_SHORT).show()

        val i = Intent(this, UserDetailActivity::class.java)
        i.putExtra(UserDetailActivity.EXTRA_USER, user.login)
        startActivity(i)
    }

    private fun getDataFavorite() {
        listFavorite = helper.queryAll()
        if (listFavorite.size > 0) {
            binding.rvUser.visibility = View.VISIBLE
            binding.tvNotFound.visibility = View.GONE
            adapter.setData(listFavorite)
        } else {
            binding.rvUser.visibility = View.GONE
            binding.tvNotFound.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        getDataFavorite()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}