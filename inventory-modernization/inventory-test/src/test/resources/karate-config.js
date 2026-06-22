function fn() {
  var env = karate.env;
  if (!env) env = 'local';

  var config = {
    env: env,
    baseUrl: 'http://localhost:8082'
  };

  if (env === 'dev') {
    config.baseUrl = 'https://inventory-dev.maersk.com';
  }

  karate.configure('headers', {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  });

  karate.configure('connectTimeout', 30000);
  karate.configure('readTimeout', 60000);

  return config;
}
