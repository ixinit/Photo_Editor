package com.photoeditor.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photoeditor.app.base.BaseActivity
import com.photoeditor.app.filter.FilterListener
import com.photoeditor.app.filter.FilterViewAdapter
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.PhotoFilter

class MainActivity2 : BaseActivity() {

    class FilterListnerResycle : FilterListener{
        override fun onFilterSelected(photoFilter: PhotoFilter?){
            //to do
        }
    }

    var recyclerView: RecyclerView? = null
    private val mFilterListner = FilterListnerResycle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        var mPhotoEditorView : PhotoEditorView = findViewById(R.id.photoEditorView)
        mPhotoEditorView.source.setImageResource(R.drawable.ic_launcher_background)
        initView()
    }
    fun initView(){
        recyclerView = findViewById(R.id.recyclerViewFilters)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        val llmFilters = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.layoutManager = llmFilters

        recyclerView?.adapter = FilterViewAdapter(mFilterListner)
    }

}