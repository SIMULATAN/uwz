ARG BUILD_FROM
FROM $BUILD_FROM

WORKDIR /app

COPY entrypoint.sh .

RUN apt update && apt install -y libcurl4-gnutls-dev && rm -rf /var/lib/apt/lists/*

COPY --from=ghcr.io/simulatan/uwz /app/uwz uwz
RUN chmod +x uwz

ENTRYPOINT [ "/app/entrypoint.sh" ]
