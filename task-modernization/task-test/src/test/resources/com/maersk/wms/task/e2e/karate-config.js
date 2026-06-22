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
  }

  config.headers = {
    'Content-Type': 'application/json',
    'X-Client-Code': config.clientCode,
    'X-Facility-Code': config.facilityCode,
    'X-User-Id': config.userId
  };

  config.generateTaskId = function() {
    return 'TASK-' + java.lang.System.currentTimeMillis();
  };

  config.generateGroupId = function() {
    return 'GRP-' + java.lang.System.currentTimeMillis();
  };

  return config;
}
