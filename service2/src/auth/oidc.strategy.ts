import { UnauthorizedException } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import {
  Strategy,
  Client,
  UserinfoResponse,
  TokenSet,
  Issuer,
} from 'openid-client';

export const buildOpenIdClient = async () => {
  const TrustIssuer = await Issuer.discover(
    'IDP_METADATA_PATH_TO/.well-known/openid-configuration',
  );
  return new TrustIssuer.Client({
    client_id: 'IDP_CLIENT_ID',
    client_secret: 'IDP_CLIENT_SECRET',
  });
};

export class OidcStrategy extends PassportStrategy(Strategy, 'oidc') {
  client: Client;

  constructor(client: Client) {
    super({
      client: client,
      params: {
        redirect_uri: 'https://localhost:3000/auth/azure',
        scope: ['openid'],
        response_types: ['id_token'],
        response_mode: 'form_post',
      },
      passReqToCallback: false,
      usePKCE: false,
    });

    this.client = client;
  }

  async validate(tokenset: TokenSet): Promise<any> {
    const userinfo: UserinfoResponse = await this.client.userinfo(tokenset);

    try {
      const id_token = tokenset.id_token;
      const access_token = tokenset.access_token;
      const refresh_token = tokenset.refresh_token;
      const user = {
        id_token,
        access_token,
        refresh_token,
        userinfo,
      };
      return user;
    } catch (err) {
      throw new UnauthorizedException();
    }
  }
}
