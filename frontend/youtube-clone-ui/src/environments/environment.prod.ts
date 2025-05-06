export const environment = {
  produection: true,
  HOST: 'http://localhost:8080',
  SERVICE_NAME: '/api/youtube',
  AUTH0_CONFIG: {
    AUTHORITY: 'https://karthick-v.us.auth0.com',
    // redirectUrl: window.location.origin,
    REDIRECT_URL: window.location.origin + '/login/callback',
    POSTLOGOUT_REDIRECTURI: window.location.origin,
    CLIENT_ID: 'w4oDolUdBgotpHMD1VLgTwNW46KDNr5E',
    SCOPE:
      'openid profile email offline_access given_name family_name nickname phone address picture',
    RESPONSE_TYPE: 'code',
    SILENT_RENEW: true,
    USE_REFRESH_TOKEN: true,
    LOGLEVEL: 1,
    SECURE_ROUTES: ['https://www.avkarthick.in/api'],
    AUDIENCE: 'https://api.avkarthick.in',
  }
}
