**Prism: Zero-Trust AI Data Privacy Shield** :    

Prism is a high-performance security gateway designed to bridge the gap between enterprise productivity and data sovereignty. As organizations rush to adopt Large Language Models (LLMs), the risk of leaking Protected Health Information (PHI), Personally Identifiable Information (PII), or trade secrets to third-party providers has become a primary blocker.

Prism acts as an intelligent, transparent proxy that sanitizes outbound prompts and reconstructs inbound responses, ensuring that sensitive data never leaves your secure perimeter.

**🚀 Key Features**

1. **Zero-Trust Architecture:** No data is stored or transmitted to the cloud without undergoing multi-stage inspection and masking.
2. **Context-Aware Redaction:** Unlike simple regex-based tools, Prism uses local NLP models to distinguish between sensitive PII and benign technical jargon.
3. **High-Throughput Processing:** Leveraging Java 21's Virtual Threads to handle thousands of concurrent data streams with minimal latency.
4. **Bi-Directional Mapping:** Transparently replaces masked tokens in AI responses to maintain a seamless user experience.

**Technologies and Tools used**

1. JDK 21
2. Ollama/Llama 3.2
3. Maven 4.0
4. Spring Boot 3.5

This project is **WIP**.
