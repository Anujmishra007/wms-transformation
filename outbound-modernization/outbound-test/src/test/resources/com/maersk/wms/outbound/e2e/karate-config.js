function fn() {
  var env = karate.env || 'dev';
  karate.log('karate.env system property was:', env);

  var config = {
    env: env,
    baseUrl: 'http://localhost:8080',
    clientCode: 'TEST',
    facilityCode: 'DC01',
    userId: 'testuser'
  };

  if (env == 'dev') {
    config.baseUrl = 'http://localhost:8080';
  } else if (env == 'staging') {
    config.baseUrl = 'http://staging.wms.maersk.com';
  } else if (env == 'prod') {
    config.baseUrl = 'http://wms.maersk.com';
  }

  // Common headers
  config.headers = {
    'Content-Type': 'application/json',
    'X-Client-Code': config.clientCode,
    'X-Facility-Code': config.facilityCode,
    'X-User-Id': config.userId
  };

  // Helper functions
  config.generateOrderNumber = function() {
    return 'ORD-' + java.lang.System.currentTimeMillis();
  };

  config.generateSku = function() {
    return 'SKU-' + Math.floor(Math.random() * 10000);
  };

  return config;
}
