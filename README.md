# GlassAssistant: Unlock the Power of AI-Powered Vision using Open Source Models

Imagine a world where you can understand the world around you, simply by looking at it. With GlassAssistant, you can harness the power of open-source AI-powered vision to unlock new possibilities. By combining the capabilities of LLava Vision from Ollama and OpenAI's Transcription capabilities, you can turn your Google Glass into a powerful tool for understanding and interacting with the world.

## Getting Started

To start your journey with GlassAssistant, follow these simple steps:
1. **Install the Essentials:** Download and install Tailscale on your hosted Ollama machine and Google Glass from F-Droid.
2. **Configure Your Ollama:** Edit line 162 on `app/src/main/java/dev/synople/glassassistant/fragments/LoadingFragment.kt` with your Tailscale Host IP Address.
3. **Run LLAVA:** Download LLAVA from Ollama on your host machine with the following args: `OLLAMA_HOST=XXX.XXX.XX.XXX:11434` ollama run llava.
4. **Start Ollama:** Start Ollama on your host machine using the following args: `OLLAMA_HOST=XXX.XXX.XX.XXX:11434` ollama run serve.
5. **Get Your OpenAI API Key:** Generate an OpenAI API Key from the OpenAI platform.
6. **Turn Your API Key into a QR Code:** Convert your API Key into a QR Code.
7. **Install and Launch the GlassAssistant App:** Install and launch the GlassAssistant app on your Google Glass.
8. **Scan the QR Code:** Scan the generated QR Code to connect your GlassAssistant app to your OpenAI API Key.
9. **Start Exploring:** Hold the camera button and speak for a custom prompt, or tap Glass to use a default prompt. The results are vertically scrollable, and you can tap Glass or click the camera button to return to the Camera view.




## Screenshots
![A diecast model car is visible through Glass EE2 with application usage instructions on screen.](./README/camera.png)

![A transcription of the spoken text is visible, with a loading animation underneath](./README/loading.png)

![White text on a black background: The primary object in the image is a small replica of a vintage convertible car, possibly a collectible or a toy, displayed on a wooden surface with a purple light illuminating the background.](./README/result.png)
