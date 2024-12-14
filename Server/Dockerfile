FROM ubuntu
WORKDIR /app
COPY src /app
RUN apt-get update && apt-get install -y python3 python3-pip dos2unix
RUN dos2unix /app/*
RUN pip install --upgrade pip
ENV ssl_client_key="/ssl/client-key.pem"
ENV ssl_client_cert="/ssl/client-cert.pem"
COPY ssl /ssl 
ENV password_mysql=$password_mysql
ENV username_mysql=$username_mysql
RUN pip install -r requirement.txt
RUN mkdir tmp
EXPOSE 80
EXPOSE 2509
CMD ["python3","-u","main.py"]
