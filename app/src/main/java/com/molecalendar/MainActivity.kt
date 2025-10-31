package com.molecalendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var moleViewModel: MoleViewModel
    private lateinit var adapter: MoleAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        emptyView = findViewById(R.id.emptyView)
        fab = findViewById(R.id.fab)

        // Setup RecyclerView
        adapter = MoleAdapter(emptyList()) { mole ->
            val intent = Intent(this, MoleDetailActivity::class.java)
            intent.putExtra("MOLE_ID", mole.id)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup ViewModel
        moleViewModel = ViewModelProvider(this)[MoleViewModel::class.java]
        moleViewModel.allMoles.observe(this) { moles ->
            moles?.let {
                adapter.updateMoles(it)
                updateEmptyView(it.isEmpty())
            }
        }

        // Setup FAB
        fab.setOnClickListener {
            val intent = Intent(this, AddMoleActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }
}
