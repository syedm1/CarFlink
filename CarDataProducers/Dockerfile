# Dockerfile for Python data stream producer

# Use an official Python runtime as a parent image
FROM python:3.10-slim

# Install librdkafka for confluent-kafka
RUN apt-get update && apt-get install -y \
    librdkafka-dev \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . .

# Install any needed packages specified in requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# Run the Python script when the container starts
CMD ["python", "caraDataStreams.py"]
