package com.eduardo.doubleclickcontainer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.eduardo.library.IDoubleClickCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val onClickContainer by lazy {
        object : IDoubleClickCallback {
            override fun onSingleClick() {
                Toast.makeText(this@MainActivity, "onSingleClick", Toast.LENGTH_SHORT).show()
            }

            override fun onDoubleClick() {
                Toast.makeText(this@MainActivity, "onDoubleClick", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        leftContainer?.clicksInterface = onClickContainer
        rightContainer?.clicksInterface = onClickContainer
    }
}
