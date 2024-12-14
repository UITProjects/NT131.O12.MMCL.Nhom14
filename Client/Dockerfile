FROM ubuntu
RUN apt-get update && apt-get install -y python3 python3-pip dos2unix git
RUN pip install --upgrade pip
WORKDIR /app
COPY src /app
RUN dos2unix /app/*
ENV symmetric_key=symmetric_key
RUN pip install -r requirement.txt
EXPOSE 80
CMD ["python3","-u","main.py"]

