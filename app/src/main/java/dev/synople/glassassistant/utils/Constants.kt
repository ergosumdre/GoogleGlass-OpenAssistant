package dev.synople.glassassistant.utils

import androidx.datastore.preferences.core.stringPreferencesKey

object GlassAssistantConstants {
    val DATASTORE_OPEN_AI_API_KEY = stringPreferencesKey("openAiApiKey")
    val DATASTORE_TAILSCALE_HOST_IP = stringPreferencesKey("tailscale_host_ip")


    val DEFAULT_PROMPT = "In one sentence, describe the primary object in this image."
}
