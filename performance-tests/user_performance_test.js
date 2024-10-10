import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '20s', target: 15 },  // Ramp up to 15 users over 20 seconds
    { duration: '1m', target: 30 },   // Stay at 30 users for 1 minute
    { duration: '20s', target: 0 },   // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% of requests should be below 500ms
  },
};

export default function () {
  // Create a new user
  let userData = JSON.stringify({
    id: Math.floor(Math.random() * 1000000),
    username: `user_${Math.floor(Math.random() * 10000)}`,
    firstName: "Test",
    lastName: "User",
    email: `test${Math.floor(Math.random() * 10000)}@example.com`,
    password: "password123",
    phone: "123456789",
    userStatus: 1,
  });

  let createUserRes = http.post('http://localhost:8080/api/v3/user', userData, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(createUserRes, {
    'Create User - is status 200': (r) => r.status === 200,
  });

  // Login with the newly created user
  let username = JSON.parse(userData).username;
  let loginRes = http.get(`http://localhost:8080/api/v3/user/login?username=${username}&password=password123`);

  check(loginRes, {
    'Login - is status 200': (r) => r.status === 200,
  });

  sleep(1);
}
