import packageInfo from '../../package.json';

export const environment = {
  appVersion: packageInfo.version,
  production: false,
  apiUrl: {
    auth: 'http://localhost:8080/api/v1/auth',
  },
};

