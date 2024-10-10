import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '20s', target: 10 },  // Ramp up to 10 users over 20 seconds
    { duration: '1m', target: 20 },   // Stay at 20 users for 1 minute
    { duration: '20s', target: 0 },   // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% of requests should be below 500ms
  },
};

export default function () {
  let petData = JSON.stringify({
    id: Math.floor(Math.random() * 1000000),
    name: `Pet_${Math.floor(Math.random() * 1000)}`,
    status: 'available',
  });

  let res = http.post('http://localhost:8080/api/v3/pet', petData, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    'is status 200 or 201': (r) => r.status === 200 || r.status === 201,
  });

  sleep(1);
}
