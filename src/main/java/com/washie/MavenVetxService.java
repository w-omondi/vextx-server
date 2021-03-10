package com.washie;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MavenVetxService extends AbstractVerticle {
    private MongoClient dbclient;
    private  static final String ITEM_TABLE ="item";
    @Override
    public void start() {

        JsonObject config = new JsonObject()
        .put("connection_string","mongodb://localhost:27017")
        .put("db_name", "tutorialdb");

        dbclient = MongoClient.createShared(this.vertx, config);

        
        Router r = Router.router(this.vertx);
        r.get("/").handler(this::home);
        r.get("/developer").handler(this::getDeveloper);
        r.get("/image").handler(this::getImage);

        
        r.route().handler(BodyHandler.create());
        r.post("/addItem").handler(this::addItem);
        this.vertx.createHttpServer()
        .requestHandler(r)
        .listen(8080);
    }

    private void home(final RoutingContext rc){
        rc.response()
        .putHeader("content-type", "text/html")
        .sendFile("Home.html");
    }

    private void addItem(final RoutingContext rc){
        String item = rc.request().getFormAttribute("itemName");
        String color= rc.request().getFormAttribute("color");

        JsonObject recieved = new JsonObject()
        .put("item", item)
        .put("color", color);

        dbclient.insert(ITEM_TABLE, recieved,res ->{
            if(res.succeeded()){
                recieved.put("Recieved","Item saved successfully");
                rc.response().end(recieved.encodePrettily());
            }else{
                rc.response().end(res.cause().getMessage());
            }
        });        
    }

    public void getImage(final RoutingContext rc){
        rc.response().sendFile("ssd.jpg");
    }
    public void getDeveloper(final RoutingContext rc){
        rc.response()
        .putHeader("content-type", "text/html")
        .sendFile("developer.html");
    }

}
