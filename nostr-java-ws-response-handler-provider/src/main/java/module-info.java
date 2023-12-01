/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

module nostr.ws.response.handler.provider {
    requires nostr.ws.handler;
    requires nostr.util;
    requires nostr.base;
    requires nostr.event;

    requires static lombok;
    requires java.logging;
    
    exports nostr.ws.response.handler.provider;
    
    uses nostr.ws.handler.command.spi.ICommandHandler;
    
    provides nostr.ws.handler.spi.IResponseHandler with nostr.ws.response.handler.provider.ResponseHandlerImpl;
}
