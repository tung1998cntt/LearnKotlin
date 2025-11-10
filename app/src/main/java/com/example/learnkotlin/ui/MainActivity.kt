package com.example.learnkotlin.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.learnkotlin.AppData
import com.example.learnkotlin.MyApp
import com.example.learnkotlin.MyViewModel
import com.example.learnkotlin.R
import com.example.learnkotlin.Tags
import com.example.learnkotlin.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), MainContract.View {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MyViewModel by viewModel()
    private val presenter: MainContract.Presenter by inject()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        presenter.attachView(this)
        initAction()
        observeData()
    }

    private fun observeData() {
        viewModel.accountLiveData.observe(this) {
            binding.tvTitle.text = it
        }
    }

    private fun initAction() {
        binding.btnTest.setOnClickListener {
            presenter.loadData()
        }
    }

    override fun showData(data: String) {
        binding.tvTitle.text = data
    }

    override fun showTransactionList(data: List<String>) {
        binding.tvTitle.text = data.toString()
    }

    override fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            val text = MyApp.getContext(AppData.getInstance().getLocale())
                .getString(R.string.transfer_in_progress)
            Log.d(Tags.TUNG, text)
        } else {
            val text = MyApp.getContext(AppData.getInstance().getLocale())
                .getString(R.string.transfer_done)
            Log.d(Tags.TUNG, text)
        }

    }

    override fun showError(message: String?) {
        Toast.makeText(this, message ?: "Unknown error", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun getContext(): Context = this

}