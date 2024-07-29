package dev.synople.glassassistant.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dev.synople.glassassistant.R
import dev.synople.glassassistant.dataStore
import dev.synople.glassassistant.utils.GlassAssistantConstants
import dev.synople.glassassistant.utils.GlassGesture
import dev.synople.glassassistant.utils.GlassGestureDetector
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.time.Duration

private val TAG = CameraFragment::class.simpleName!!

class LoadingFragment : Fragment() {
    private val args: LoadingFragmentArgs by navArgs()
    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(Duration.ofSeconds(30))
        .build()

    private var openAiApiKey = ""
    private var tailscale_host_ip = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_loading, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            requireContext().dataStore.data.firstOrNull { true }?.let { value ->
                openAiApiKey = value[GlassAssistantConstants.DATASTORE_OPEN_AI_API_KEY] ?: ""
                tailscale_host_ip = value[GlassAssistantConstants.DATASTORE_TAILSCALE_HOST_IP] ?: ""
            }

            args.recorderFile?.let {
                getSpeechResponse(it)
            } ?: getVisionResponse(GlassAssistantConstants.DEFAULT_PROMPT, args.imageFile)
        }

        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onGesture(glassGesture: GlassGesture) {
        when (glassGesture.gesture) {
            GlassGestureDetector.Gesture.SWIPE_DOWN -> {
                requireView().findNavController()
                    .navigate(R.id.action_loadingFragment_to_cameraFragment)
            }

            else -> {}
        }
    }

    private fun getSpeechResponse(recorderFile: File) {
        val speechRequestPayload = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                recorderFile.name,
                recorderFile.asRequestBody("audio/mp4".toMediaTypeOrNull())
            )
            .addFormDataPart("model", "whisper-1")
            .build()
        val openAiSpeechRequest = Request.Builder()
            .url("https://api.openai.com/v1/audio/transcriptions")
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("Authorization", "Bearer $openAiApiKey")
            .post(speechRequestPayload)
            .build()
        okHttpClient.newCall(openAiSpeechRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed while calling OpenAI Speech", e)
            }

            override fun onResponse(call: Call, response: Response) {
                recorderFile.delete()
                val responseText = response.body?.string() ?: ""
                Log.d(TAG, "OpenAI Speech Response: $responseText")

                try {
                    val jsonResponse = JSONObject(responseText)
                    if (jsonResponse.has("text")) {
                        val content = jsonResponse.getString("text")
                        requireActivity().runOnUiThread {
                            requireView().findViewById<TextView>(R.id.tvLoading).text = content
                        }
                        getVisionResponse(content, args.imageFile)
                    } else {
                        Log.e(TAG, "No 'text' field in the response")
                        requireActivity().runOnUiThread {
                            requireView().findViewById<TextView>(R.id.tvLoading).text = "Failed to get response text"
                        }
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "Failed to parse response", e)
                    requireActivity().runOnUiThread {
                        requireView().findViewById<TextView>(R.id.tvLoading).text = "Failed to parse response"
                    }
                }
            }
        })
    }

    private fun getVisionResponse(prompt: String, imageFile: File) {
        // Load imageFile to Base64 string
        val base64Image =
            requireContext().contentResolver.openInputStream(imageFile.toUri()).use {
                val bitmap = BitmapFactory.decodeStream(it)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            }
        imageFile.delete()

        // Create the request payload
        val visionRequestPayload = JSONObject().apply {
            put("model", "llava")
            put("prompt", prompt)
            put("stream", false)
            put("images", JSONArray().put(base64Image))
        }

        // Create the request
        val visionRequest = Request.Builder()
            .url("$tailscale_host_ip:11434/api/generate")
            .addHeader("Content-Type", "application/json")
            .post(visionRequestPayload.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        okHttpClient.newCall(visionRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed while calling custom vision API", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseText = response.body?.string() ?: ""
                Log.d(TAG, "Custom Vision API Response: $responseText")

                try {
                    val jsonResponse = JSONObject(responseText)
                    val content = if (jsonResponse.has("response")) {
                        jsonResponse.getString("response")
                    } else {
                        "No 'response' field found in JSON"
                    }

                    requireActivity().runOnUiThread {
                        requireView().findNavController().navigate(
                            LoadingFragmentDirections.actionLoadingFragmentToResultFragment(content)
                        )
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "Failed to parse JSON response", e)
                    requireActivity().runOnUiThread {
                        requireView().findViewById<TextView>(R.id.tvLoading).text = "Failed to parse JSON response"
                    }
                }
            }
        })
    }
}
