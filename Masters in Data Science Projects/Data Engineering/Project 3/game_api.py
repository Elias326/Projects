#!/usr/bin/env python
import json
from kafka import KafkaProducer
from flask import Flask, request

app = Flask(__name__)
producer = KafkaProducer(bootstrap_servers='kafka:29092')


def log_to_kafka(topic, events):
    events.update(request.headers)
    producer.send(topic, json.dumps(events).encode())


@app.route("/")
def default_response():
    default_event = {'event_type': 'default'}
    log_to_kafka('events', default_event)
    return "Welcome to Legends of Zion!\n"


@app.route("/purchase_sword")
def purchase_sword():
    purchase_sword_event = {'event_type': 'purchase_sword'}
    log_to_kafka('events', purchase_sword_event)
    return "Sword Purchased!\n"

@app.route("/purchase_a_shield")
def purchase_shield():
    purchase_shield_event = {'event_type': 'purchase_shield'}
    log_to_kafka('events', purchase_shield_event)
    return "Shield Purchased!\n"

@app.route("/join_guild")
def join_guild():
    join_guild_event = {'event_type': 'join_guild'}
    log_to_kafka('events', join_guild_event)
    return "Guild Joined!\n"

@app.route("/purchase_a_potion")
def purchase_potion():
    purchase_potion_event = {'event_type': 'purchase_potion'}
    log_to_kafka('events', purchase_potion_event)
    return "Potion Purchased!\n"

