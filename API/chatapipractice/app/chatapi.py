import openai


def chatapi(message):
    openai.api_key = open("key.txt", "r").read().strip("\n")

    messages = [
        # system message first, it helps set the behavior of the assistant
        {"role": "system", "content": "You are a helpful assistant."},
    ]

    while True:
        if message:
            messages.append(
                {"role": "user", "content": message},
            )
            chat_completion = openai.ChatCompletion.create(
                model="gpt-3.5-turbo", messages=messages
            )

        reply = chat_completion.choices[0].message.content
        """print(f"🤖: {reply}")"""
        messages.append({"role": "assistant", "content": reply})
        return reply
