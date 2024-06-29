FROM gradle:8.7-focal AS builder

WORKDIR /app

RUN apt update && apt install -y libcurl4-gnutls-dev && rm -rf /var/lib/apt/lists/*

COPY module.yaml settings.gradle.kts ./

RUN gradle compileKotlinLinuxX64 --no-daemon

COPY src@linuxX64/ src

RUN gradle linuxX64MainBinaries --no-daemon

FROM ubuntu:focal

RUN apt update && apt install -y libcurl4-gnutls-dev && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/build/bin/linuxX64/linuxX64ReleaseExecutable/kotlin.kexe uwz

ENTRYPOINT ["./uwz"]
