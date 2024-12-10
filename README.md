# Google Glass Meets Open Source Multimodal Models

Imagine a world where you can understand the world around you, simply by looking at it. With Google Glass Open Source Assistant, you can harness the power of open-source AI-powered vision to unlock new possibilities. By combining the capabilities of LLava Vision from Ollama and OpenAI's Transcription capabilities, you can turn your Google Glass into a powerful tool for understanding and interacting with the world.


## Getting Started

To make Google Glass run Open Source Models, follow these simple steps:
1. **Install the Essentials:** Download and install Tailscale on your hosted Ollama machine and Google Glass from F-Droid. Create an account, sign in on both Google Glass and the hosted machine. This allows you to remotely connect to your local Ollama. Copy the hosted machine's Tailscale IP Address
2. **Configure Your Ollama:** Follow steps here to configure your Ollama host to the Tailscale IP Address: https://github.com/ollama/ollama/blob/main/docs/faq.md
3. **Run LLAMA VISION:** Download LLAVA from Ollama on your host machine with the following args: `OLLAMA_HOST=<TAILSCALE_IP_ADDRESS>:11434 ollama pull llama3.2-vision:11b`.
4. **Start Ollama:** Start Ollama on your host machine using the following args: `OLLAMA_HOST=<TAILSCALE_IP_ADDRESS>:11434 ollama serve`.
5. **Get Your OpenAI API Key:** Generate an OpenAI API Key from the OpenAI platform.
6. **Turn Your API Key into a QR Code:** Convert your API Key into a QR Code.
7. **Turn Your Tailscale IP Address into a QR Code:** Convert your <TAILSCALE_IP_ADDRESS> into a QR Code.
8. **Install and Launch the GlassAssistant App:** Install and launch the GlassAssistant app on your Google Glass.
9. **Scan the QR Code:** Scan the generated QR Code to connect your GlassAssistant app to your OpenAI API Key.
10. **Scan the QR Code:** Scan the generated QR Code to connect your GlassAssistant app to your Tailscale IP Address.
11. **Start Exploring:** Hold the camera button and speak for a custom prompt, or tap Glass to use a default prompt. The results are vertically scrollable, and you can tap Glass or click the camera button to return to the Camera view.




## Screenshots
![A diecast model car is visible through Glass EE2 with application usage instructions on screen.](./README/camera.png)

![A transcription of the spoken text is visible, with a loading animation underneath](./README/loading.png)

![White text on a black background: The primary object in the image is a small replica of a vintage convertible car, possibly a collectible or a toy, displayed on a wooden surface with a purple light illuminating the background.](./README/result.png)
