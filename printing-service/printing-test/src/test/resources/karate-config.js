function fn() {
  var env = karate.env;
  karate.log('karate.env:', env);

  if (!env) {
    env = 'local';
  }

  var config = {
    env: env,
    baseUrl: 'http://localhost:8091',
    clientCode: 'NIKE',
    countryCode: 'KR',
    warehouseCode: 'KRIC01',
    userId: 'TEST_USER'
  };

  if (env === 'dev') {
    config.baseUrl = 'https://printing-dev.maersk.com';
  } else if (env === 'staging') {
    config.baseUrl = 'https://printing-staging.maersk.com';
  }

  // Common headers
  karate.configure('headers', {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'X-Client-Code': config.clientCode,
    'X-Country-Code': config.countryCode,
    'X-Warehouse-Code': config.warehouseCode,
    'X-User-Id': config.userId
  });

  karate.configure('connectTimeout', 30000);
  karate.configure('readTimeout', 60000);

  return config;
}
