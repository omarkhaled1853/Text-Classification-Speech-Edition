/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mediapipe.examples.textclassifier

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.mediapipe.examples.textclassifier.databinding.ActivityMainBinding
import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult
import java.util.Locale

// Extending MainActivity TextToSpeech.OnInitListener class
class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    /* =============================================== ADDED ======================================== */
    //Making instance of TextToSpeech to perform text to speach conversion
    private var tts: TextToSpeech? = null
    
    private var _activityMainBinding: ActivityMainBinding? = null
    private val activityMainBinding get() = _activityMainBinding!!
    private lateinit var classifierHelper: TextClassifierHelper
    private val adapter by lazy {
        ResultsAdapter()
    }

    private val listener = object :
        TextClassifierHelper.TextResultsListener {
        override fun onResult(
            results: TextClassifierResult,
            inferenceTime: Long
        ) {
            runOnUiThread {
                activityMainBinding.bottomSheetLayout.inferenceTimeVal.text =
                    String.format("%d ms", inferenceTime)

                adapter.updateResult(results.classificationResult()
                    .classifications().first()
                    .categories().sortedByDescending {
                        it.score()
                    }, classifierHelper.currentModel
                )

    /* =============================================== ADDED ======================================== */
                //Getting categoryName of index 0 from categories list   
                var categoryName = results.classificationResult()
                    .classifications().first()
                    .categories()[0].categoryName()
                //Getting categoryScore of index 0 from categories list  
                var categoryScore = results.classificationResult()
                    .classifications().first()
                    .categories()[0].score()

                //Checking categoryName and its score if greater than 0.5f to speech postive or negative as
                //CategoryName "1" => is Positive
                //CategoryName "0" => is Negative
                if (categoryName.equals("1") && categoryScore > 0.5f){
                    speakOut("Positive")
                }
                else if (categoryName.equals("0") && categoryScore > 0.5f){
                    speakOut("Negative")
                }
            }
        }

        override fun onError(error: String) {
            Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* =============================================== ADDED ======================================== */
        //Initializes the TextToSpeech engine and passes this as both the context and the OnInitListener
        tts = TextToSpeech(this, this)

        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // Create the classification helper that will do the heavy lifting
        classifierHelper = TextClassifierHelper(
            context = this@MainActivity,
            listener = listener
        )

        // Classify the text in the TextEdit box (or the default if nothing is added)
        // on button click.
        activityMainBinding.classifyBtn.setOnClickListener {
            if (activityMainBinding.inputText.text.isNullOrEmpty()) {
                classifierHelper.classify(getString(R.string.default_edit_text))
            } else {
                classifierHelper.classify(activityMainBinding.inputText.text.toString())
            }
        }

        activityMainBinding.results.adapter = adapter
        initBottomSheetControls()
    }

    private fun initBottomSheetControls() {
        val behavior =
            BottomSheetBehavior.from(activityMainBinding.bottomSheetLayout.bottomSheetLayout)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Allows the user to switch between the classification models that are available.
        activityMainBinding.bottomSheetLayout.modelSelector.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.wordvec -> {
                    classifierHelper.currentModel =
                        TextClassifierHelper.WORD_VEC
                    classifierHelper.initClassifier()
                }
                R.id.mobilebert -> {
                    classifierHelper.currentModel =
                        TextClassifierHelper.MOBILEBERT
                    classifierHelper.initClassifier()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    /* =============================================== ADDED ======================================== */
    //Override onInit from TextToSpeech.OnInitListener
    override fun onInit(status: Int) {
        //Checks if the initialization was successful
        if (status == TextToSpeech.SUCCESS) {
            // Set language to US English
            val result = tts!!.setLanguage(Locale.US)

            //Checks if the language data is missing or not supported
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Log error if language not supported
                Log.e("TTS","The Language not supported!")
            }
        }
    }

    /* =============================================== ADDED ======================================== */
    //SpeakOut function that speech given text
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }
}
