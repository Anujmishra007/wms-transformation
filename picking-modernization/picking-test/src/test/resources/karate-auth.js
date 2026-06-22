function fn() {
  var env = karate.get('env');

  // For local testing, return mock token
  if (env === 'local') {
    return {
      token: 'Bearer mock-jwt-token-for-testing',
      userId: 'TEST_USER',
      countryCode: 'KR',
      clientCode: 'NIKE'
    };
  }

  // For other environments, get real token
  var credentials = {
    username: karate.properties['test.username'] || 'test_user',
    password: karate.properties['test.password'] || 'test_password'
  };

  var response = karate.call('classpath:auth/get-token.feature', credentials);

  return {
    token: 'Bearer ' + response.accessToken,
    userId: response.userId,
    countryCode: response.countryCode,
    clientCode: response.clientCode
  };
}
