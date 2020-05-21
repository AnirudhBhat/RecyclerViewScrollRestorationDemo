package com.abhat.recyclerviewscrollpositiondemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val recyclerAdapter by lazy {
        RecyclerAdapter(listOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        showProgressBar()
        doSomeDummyNetworkCallUsingDelay()
    }

    private fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }

    private fun doSomeDummyNetworkCallUsingDelay() {
        CoroutineScope(mainScope.coroutineContext).launch {
            supervisorScope {
                dummyNetworkCall()
            }
        }
    }

    private suspend fun dummyNetworkCall() {
        supervisorScope {
            async {
                delay(2000)
            }.await()
            hideProgressBar()
            recyclerAdapter.updateList(populateAndReturnList())
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun populateAndReturnList(): List<String> {
        var position = 0
        val demoList = mutableListOf<String>()
        repeat(50) {
            demoList.add("Title $position")
        }
        return demoList
    }

    private fun setupRecyclerView() {
        rv_title.layoutManager = LinearLayoutManager(this)
        rv_title.adapter = recyclerAdapter
        rv_title.adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }
}
