import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '20s', target: 15 },  // Ramp up to 15 users over 20 seconds
    { duration: '1m', target: 30 },   // Stay at 30 users for 1 minute
    { duration: '20s', target: 0 },   // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<600'],  // 95% of requests should be below 600ms
  },
};

export default function () {
  let orderData = JSON.stringify({
    id: Math.floor(Math.random() * 1000000),
    petId: Math.floor(Math.random() * 10000),
    quantity: 1,
    shipDate: new Date().toISOString(),
    status: 'placed',
    complete: true,
  });

  let res = http.post('http://localhost:8080/api/v3/store/order', orderData, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    'is status 200 or 201': (r) => r.status === 200 || r.status === 201,
  });

  sleep(1);
}
