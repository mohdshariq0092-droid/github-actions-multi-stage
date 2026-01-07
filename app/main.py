from flask import Flask, jsonify
import os

app = Flask(__name__)


@app.route("/health")
def health():
    return jsonify({"status": "ok"}), 200


if __name__ == "__main__":  # pragma: no cover
    host = os.getenv("APP_HOST", "127.0.0.1")
    port = int(os.getenv("APP_PORT", 7000))
    app.run(host=host, port=port)
