# AEGIS: AI Edge Gateway for Inspection & Security

A high-performance, pure-Java LLM Security Gateway (LLM-WAF) that protects enterprise AI applications from prompt injections, jailbreaks, and sensitive data leakage in real time.

## Overview

AEGIS is built entirely on the JVM (Java 21 + Spring Boot 3). It acts as a drop-in reverse proxy between your application and the LLM API (like OpenAI), intercepting requests to run fast heuristic checks and outbound scanning.

## Architecture

- **Gateway Service:** Spring Cloud Gateway based proxy.
- **Inspection Service:** Heuristics and fast embedding-based prompt analysis.
- **Outbound Scanner:** Regex + entropy scanning for PII and secrets on model responses.
- **Policy Service:** Tenant rules and configurations.
- **Telemetry Service:** Audit logging and metrics via Kafka.

## Getting Started

*(Instructions for local development via Docker Compose to be added...)*
