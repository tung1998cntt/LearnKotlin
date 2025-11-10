package com.example.learnkotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.learnkotlin.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MyViewModel by viewModel()

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
        initAction()
        observeData()
    }

    private fun observeData() {
        viewModel.accountLiveData.observe(this) {
            binding.tvTitle.text = it
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.compositeState.collect { uiState ->
                        Log.d(Tags.TUNG1, uiState.statusText.toString())
                        Log.d(Tags.TUNG1, uiState.balance.toString())
                        Log.d(Tags.TUNG1, uiState.transactions.toString())
                    }
                }

                launch {
                    viewModel.searchResult.collect { list ->
                        Log.d(Tags.TUNG1, list.toString())
                    }
                }
            }
        }

    }

    private fun initAction() {
        binding.btnTest.setOnClickListener {
            viewModel.fetchDataFlow()
            viewModel.fetchBalance()
            viewModel.fetchTransactions()
        }
        binding.edtSearch.addTextChangedListener {editable ->
            viewModel.setSearchQuery(editable.toString())
        }
    }


}