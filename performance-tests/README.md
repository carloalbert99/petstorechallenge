### Performance Tests

The performance tests are implemented using [K6](https://k6.io/), which is a popular open-source load testing tool.

#### Running the Performance Tests

1. **Install K6**:
   ```sh
   brew install k6  # For macOS, or follow the instructions for your OS here: https://k6.io/docs/getting-started/installation/
   ```

2. **Navigate to the Performance Tests Directory**:
   ```sh
   cd performance-tests
   ```

3. **Run the K6 Scripts**:
   - To run the pet creation load test:
     ```sh
     k6 run create_pet_test.js
     ```
   - To run the order creation load test:
     ```sh
     k6 run order_pet_test.js
     ```
   - To run the user creation and login test:
     ```sh
     k6 run user_performance_test.js
     ```

### Performance Results Summary

The following table summarizes the main performance metrics for each scenario:

| **Scenario**          | **Average Response Time (ms)** | **Peak Users** | **Errors (%)** | **Threshold Compliance** |
|-----------------------|--------------------------------|----------------|----------------|--------------------------|
| Create Pet            | 3.81                           | 20             | 0%             | Passed                   |
| Order a Pet           | 3.39                           | 30             | 4.36%          | Partially Passed         |
| Create User           | 2.43                           | 30             | 24.84%         | Failed                   |
| Login User            | 2.14                           | 30             | 0%             | Passed                   |

- **Create Pet Endpoint**: Handled the load efficiently, with 0% errors.
- **Order Pet Endpoint**: Had minor issues, with 4.36% of requests failing due to potential race conditions.
- **Create User Endpoint**: High failure rate (24.84%) suggests concurrency issues, such as conflicting user IDs.
- **Recommendations**:
  - Improve concurrency handling for `Create User` by ensuring unique IDs are used.
  - Implement retry logic for transient errors.

### How to Contribute

1. Fork the repository.
2. Create a feature branch:
   ```sh
   git checkout -b feature/new-feature
   ```
3. Commit your changes and push to GitHub.
4. Create a Pull Request.