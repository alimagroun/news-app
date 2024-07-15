import packageInfo from '../../package.json';

export const environment = {
  appVersion: packageInfo.version,
  production: true,
  apiUrl: {
    auth: '/api/v1/auth',
  },
};
