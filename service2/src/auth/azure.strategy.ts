import { OIDCStrategy } from 'passport-azure-ad';
import { PassportStrategy } from '@nestjs/passport';
import { Injectable } from '@nestjs/common';

@Injectable()
export class AzureStrategy extends PassportStrategy(OIDCStrategy) {
  constructor() {
    super({
      identityMetadata: 'IDP_METADATA_PATH_TO/.well-known/openid-configuration',
      clientID: 'IDP_CLIENT_ID',
      clientSecret: 'IDP_CLIENT_SECRET',
      responseType: 'id_token',
      responseMode: 'form_post',
      redirectUrl: 'https://localhost:3000/auth/azure',
      passReqToCallback: false,
      useCookieInsteadOfSession: true,
      cookieEncryptionKeys: [
        { key: 'qnys3pspvKLEdOH2hnjGCceEhT5LRuS2', iv: 'D5sJHeBjKrk7' },
      ],
      loggingNoPII: false,
      loggingLevel: 'info',
    });
  }

  async validate(data) {
    // do some logic to validate user data returned from azure

    console.log(data);
    return {
      name: data.displayName,
      email: data._json.email,
    };
  }
}
