export const environment = {
  produection: true,
  HOST: 'http://localhost:8080',
  SERVICE_NAME: '/api/youtube',
  AUTH0_CONFIG: {
    AUTHORITY: 'https://karthick-v.us.auth0.com',
    // redirectUrl: window.location.origin,
    REDIRECT_URL: 'http://52.66.117.88/login/callback',
    POSTLOGOUT_REDIRECTURI: 'http://52.66.117.88',
    CLIENT_ID: 'w4oDolUdBgotpHMD1VLgTwNW46KDNr5E',
    SCOPE:
      'openid profile email offline_access given_name family_name nickname phone address picture',
    RESPONSE_TYPE: 'code',
    SILENT_RENEW: true,
    USE_REFRESH_TOKEN: true,
    LOGLEVEL: 1,
    SECURE_ROUTES: ['http://52.66.117.88:8080/'],
    AUDIENCE: 'http://52.66.117.88:8080',
  },
};
