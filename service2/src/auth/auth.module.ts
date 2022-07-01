import { Module } from '@nestjs/common';
import { AzureStrategy } from './azure.strategy';
import {AzureController} from './azure.controller';
import { PassportModule } from '@nestjs/passport';
import { JwtModule } from '@nestjs/jwt';
import { jwtConstants } from './constants';
import { JwtStrategy } from './jwt.strategy';
import { OidcStrategy, buildOpenIdClient } from './oidc.strategy';
import { SessionSerializer } from './session.serializer';

const OidcStrategyFactory = {
  provide: 'OidcStrategy',
  useFactory: async () => {
    const client = await buildOpenIdClient(); // secret sauce! build the dynamic client before injecting it into the strategy for use in the constructor super call.
    const strategy = new OidcStrategy(client);
    return strategy;
  }
};
@Module({
  imports: [
    PassportModule.register({ defaultStrategy: 'oidc' }),
    JwtModule.register({
      secret: jwtConstants.secret,
      signOptions: { expiresIn: '360s' },
    }),
  ],
  controllers: [AzureController],
  providers: [OidcStrategyFactory, SessionSerializer, JwtStrategy]
})
export class AuthModule {}
