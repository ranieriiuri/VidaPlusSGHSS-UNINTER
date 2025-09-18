import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 200,          // 200 usuários virtuais
    duration: '30s',   // duração do teste
};

export default function () {
    // URL do endpoint
    const url = 'http://localhost:8080/consultas';

    // Headers, incluindo token Bearer se necessário
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW5pZXJpaXVyaUB0ZXN0ZS5jb20iLCJpYXQiOjE3NTgxNTUzNTEsImV4cCI6MTc1ODI0MTc1MX0.XflyZqixq_pXuqkxqWPtebnCBs8AjR9uWsN1lypvcQE'
    };

    // Corpo da requisição
    const payload = JSON.stringify({
        pacienteId: 7,
        medicoId: 6,
        data: '2025-12-12',
        hora: '14:00',
        valor: 150.00
    });

    // Enviar POST
    const res = http.post(url, payload, { headers });

    // Validar resposta
    check(res, {
        'status 200': (r) => r.status === 200,
        'sem erro': (r) => !r.body.includes('error')
    });

    sleep(0.1); // tempo entre requisições de cada usuário
}
