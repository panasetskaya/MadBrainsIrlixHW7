package com.example.madbrainsirlixhw7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    lateinit var textViewResult: TextView
    private var resultText: String? = null
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewResult = findViewById(R.id.textViewResult)
        progressBar = findViewById(R.id.progressBarEx)
        progressBar.visibility = View.VISIBLE
        if (savedInstanceState!=null) {
            resultText = savedInstanceState.getString("result").toString()
            progressBar.visibility = View.GONE
            textViewResult.text = resultText
        } else {
            // comment out one of the following:
            useThread()
            //useExecutor()
        }
    }

    fun useExecutor() {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute{
            workingBlock()
        }
    }

    fun useThread() {
        val thread = Thread {
            workingBlock()
        }
        thread.start()
    }

    fun workingBlock() {
        try {
            Thread.sleep(5000)
            resultText = parseJson()
            runOnUiThread {
                textViewResult.text = resultText
                progressBar.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("result", resultText)
        super.onSaveInstanceState(outState)
    }

    fun parseJson(): String {
        var rawAsString: String?
        val rawInputStream = resources.openRawResource(R.raw.concurrencyjson)
        val reader = BufferedReader(InputStreamReader(rawInputStream))
        try {
            val results = StringBuilder()
            while (true) {
                val line = reader.readLine() ?: break
                results.append(line)
                results.append("\n")
            }
            rawAsString = results.toString()
        } finally {
            reader.close()
        }
        val jsonArray = JSONArray(rawAsString)
        val latinList = mutableListOf<Latina>()
        var finalText = ""
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val userId = jsonObject.getString("userId")
            val id = jsonObject.getString("id")
            val body = jsonObject.getString("body")
            val title = jsonObject.getString("title")
            val latina = Latina(userId, id, title, body)
            latinList.add(latina)
            finalText = finalText + "${latina.title}: \n${latina.body}" + "\n\n\n"
        }
        return finalText
    }
}