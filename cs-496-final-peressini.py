from google.appengine.ext import ndb
import webapp2
import json
import logging
import sys
import datetime
from webapp2_extras import jinja2

class Hero(ndb.Model):
    id = ndb.StringProperty()
    name = ndb.StringProperty(required=True)
    base_of_operations = ndb.StringProperty(required=True)
    race = ndb.StringProperty(required=True) 
    main_superpower = ndb.StringProperty(required=True)

class Team(ndb.Model):
    id = ndb.StringProperty()
    name = ndb.StringProperty(required=True)
    heroes = ndb.BlobProperty(repeated=True, indexed=True)
    assembled_date = ndb.DateProperty(auto_now_add=True)
    leader = ndb.StringProperty(default=None) 

class HeroHandler(webapp2.RequestHandler):

    def post(self):
        hero_data = json.loads(self.request.body)
        if hero_data['name'] == '':
            msg = 'A hero must have a name.'
            self.abort(409, msg)
        query = Hero.query(Hero.name == hero_data['name'])
        hero_list = list(query.fetch())
        if(len(hero_list) > 0):
            message = 'A hero named "%s" already exists.' % hero_data['name']
            self.abort(409, message)
        else:
            new_hero = Hero(name=hero_data['name'], 
                            base_of_operations=hero_data['base_of_operations'],
                            race=hero_data['race'], main_superpower=hero_data['main_superpower'])
            hero_key = new_hero.put()
            new_hero = hero_key.get()
            new_hero.id = new_hero.key.urlsafe()
            new_hero.put()
            hero_dict = new_hero.to_dict()
            hero_dict['self'] = '/heroes/' + new_hero.id
            self.response.write(json.dumps(hero_dict))
    
    def get(self, id=None):

        if id:
            hero = ndb.Key(urlsafe=id).get()            
            hero_dict = hero.to_dict()
            hero_dict['self'] = "/heroes/" + id
            self.response.write(json.dumps(hero_dict))

        elif self.request.get('name'):
            self.response.write(self.request.get('name'))

        else:
            query = Hero.query()
            heroes = query.fetch()
            heroes = sorted(heroes, key=lambda hero: hero.name)
            hero_list = []
            for hero in heroes:
                hero_dict = hero.to_dict()
                hero_dict['self'] = "/heroes/" + hero.id
                hero_list.append(hero_dict)
            self.response.write(json.dumps(hero_list))

    def delete(self, id=None):
        #hero_data = json.loads(self.request.body)
        if id:
            hero = ndb.Key(urlsafe=id).get()
            teams = Team.query(Team.heroes == str(hero.name)).fetch()
            for team in teams:
                for x in team.heroes:
                    if x == str(hero.name):
                        print x
                        team.heroes.remove(x)
                team.put()
            teams = Team.query(Team.leader == str(hero.name)).fetch()
            for team in teams:
                team.leader = ''
                team.put()
            heroName = hero.name
            hero.key.delete()
            msg = 'Hero named %s has been deleted' % heroName
            self.response.write(msg)

        elif self.request.get('name'): #'name' in hero_data:
            name = self.request.get('name') #hero_data['name']
            hero = Hero.query(Hero.name == name).get()
            if hero:
                teams = Team.query(Team.heroes == str(hero.name)).fetch()
                for team in teams:
                    for x in team.heroes:
                        if x == str(hero.name):
                            print x
                            team.heroes.remove(x)
                    team.put()
                teams = Team.query(Team.leader == str(hero.name)).fetch()
                for team in teams:
                    team.leader = ''
                    team.put()
                heroName = hero.name
                hero.key.delete()
                msg = 'Hero named %s has been deleted' % heroName
                self.response.write(msg)
            else:
                msg = 'That hero does not exist.'
                self.response.write(msg)


    def patch(self, id=None):
        if id:
            hero = ndb.Key(urlsafe=id).get()
            hero_data = json.loads(self.request.body)
            if 'name' in hero_data:
                if hero_data['name'] == '' or hero_data['name'] == hero.name:
                    pass
                else:
                    query = Hero.query(Hero.name == hero_data['name'])
                    hero_list = list(query.fetch())
                    if(len(hero_list) > 0):
                        message = 'A hero with name "%s" already exists.' % hero_data['name']
                        self.abort(409, message)
                    else:
                        hero.name = hero_data['name']
            if 'base_of_operations' in hero_data:
                if hero_data['base_of_operations'] == '':
                    pass
                else:
                    hero.base_of_operations = hero_data['base_of_operations']
            if 'race' in hero_data:
                if hero_data['race'] == '':
                    pass
                else:
                    hero.race = hero_data['race']
            if 'main_superpower' in hero_data:
                if hero_data['main_superpower'] == '':
                    pass
                else:
                    hero.main_superpower = hero_data['main_superpower']
            hero.put()
            hero_dict = hero.to_dict()
            hero_dict['self'] = "/heroes/" + hero.id
            self.response.write(json.dumps(hero_dict))
            #msg = 'Boat with id %s now has name: %s, type: %s, length: %d.' % (boat.boat_id, boat.boat_name, boat.boat_type, boat.boat_length)
            #self.response.write(msg)


class TeamHandler(webapp2.RequestHandler):

    def get(self, id=None):

        if id:
            team = ndb.Key(urlsafe=id).get()
            team_dict = team.to_dict()
            team_dict['self'] = "/teams/" + id
            self.response.write(json.dumps(team_dict, indent=4, sort_keys=True, default=str))

        else:
            query = Team.query()
            teams = query.fetch()
            teams = sorted(teams, key=lambda team: team.name)
            team_list = []
            for team in teams:
                team_dict = team.to_dict()
                team_dict['self'] = "/teams/" + team.id
                team_list.append(team_dict)
            self.response.write(json.dumps(team_list, indent=4, sort_keys=True, default=str))

    def post(self):
        team_data = json.loads(self.request.body)
        if team_data['name'] == '':
            msg = 'A team must have a name.'
            self.abort(409, msg)
        query = Team.query(Team.name == team_data['name'])
        team_list = list(query.fetch())
        heroes_list = []
        if 'heroes' in team_data:
            for hero in team_data['heroes']:
                if len(Hero.query(Hero.name == hero).fetch()) == 1:
                    heroes_list.append(str(hero))
        if(len(team_list) > 0):
            message = 'A team named "%s" already exists.' % team_data['name']
            self.abort(409, message)
        else:
            new_team = Team(name=team_data['name'], heroes=heroes_list)
            if 'leader' in team_data:
                if len(Hero.query(Hero.name == team_data['leader']).fetch()) == 1:
                    if team_data['leader'] in heroes_list:
                        new_team.leader = team_data['leader']
            team_key = new_team.put()
            new_team = team_key.get()
            new_team.id = new_team.key.urlsafe()
            new_team.put()
            team_dict = new_team.to_dict()
            team_dict['self'] = '/teams/' + new_team.id 
            self.response.write(json.dumps(team_dict, indent=4, sort_keys=True, default=str))

    def patch(self, id=None):
        if id:
            team = ndb.Key(urlsafe=id).get()
            team_data = json.loads(self.request.body)
            if 'name' in team_data:
                if team_data['name'] == '' or team_data['name'] == team.name:
                    pass
                else:
                    query = Team.query(Team.name == team_data['name'])
                    team_list = list(query.fetch())
                    if(len(team_list) > 0):
                        message = 'A team named "%s" already exists.' % team_data['name']
                        self.abort(409, message)
                    else:
                        team.name = team_data['name']
            if 'heroes' in team_data:
                hero_list = []
                for hero in team_data['heroes']:
                    if len(Hero.query(Hero.name == hero).fetch()) == 1:
                        hero_list.append(str(hero))
                team.heroes = hero_list
            if 'assembled_date' in team_data:
                if team_data['assembled_date'] == '':
                    pass
                else:
                    team.assembled_date = datetime.datetime.strptime(team_data['assembled_date'], '%Y%m%d')
            if 'leader' in team_data:
                if team_data['leader'] == '':
                    pass
                else:
                    if len(Hero.query(Hero.name == team_data['leader']).fetch()) == 1:
                        if team_data['leader'] in team.heroes:
                            team.leader = team_data['leader']
            team.put()
            team_dict = team.to_dict()
            team_dict['self'] = "/teams/" + team.id
            self.response.write(json.dumps(team_dict, indent=4, sort_keys=True, default=str))

    def delete(self, id=None):
        #team_data = json.loads(self.request.body)
        if id:
            team = ndb.Key(urlsafe=id).get()
            teamName = team.name
            team.key.delete()
            msg = 'Team named %s has been deleted' % teamName
            self.response.write(msg)
        
        elif self.request.get('name'): #name' in team_data:
            name = self.request.get('name') #team_data['name']
            team = Team.query(Team.name == name).get()
            if team:
                team.key.delete()
                msg = 'Team named %s has been deleted' % name
                self.response.write(msg)
            else:
                msg = 'That team does not exist.'
                self.response.write(msg)

class SoloHandler(webapp2.RequestHandler):
    def put(self, id=None):
        if id:
            hero = ndb.Key(urlsafe=id).get()
            hero_teams = Team.query(Team.heroes == str(hero.name)).fetch()
            if len(hero_teams) > 0:
                for team in hero_teams:
                    for x in team.heroes:
                        if x == str(hero.name):
                            #print x
                            team.heroes.remove(x)
                    team.put()
            teams = Team.query(Team.leader == str(hero.name)).fetch()
            if len(teams) > 0:
                for team in teams:
                    team.leader = ''
                    team.put()
            msg = '%s now works alone' % hero.name
            self.response.write(msg)

class TeamUpHandler(webapp2.RequestHandler):
    def put(self, id=None):
        if id:
            hero = ndb.Key(urlsafe=id).get()
            team_data = json.loads(self.request.body)
            if 'name' in team_data:
                hero_teams = Team.query(Team.heroes == str(hero.name)).fetch()
                team = Team.query(Team.name == team_data['name']).get()
                if team:
                    if len(hero_teams) > 0:
                        for t in hero_teams:
                            if t.name == team.name:
                                msg = '%s is already a member of %s' % (hero.name, team.name)
                                self.abort(409, msg)
                    team.heroes.append(str(hero.name))
                    team.put()
                    msg = '%s is now a member of %s' % (hero.name, team.name)
                    self.response.write(msg)
                else:
                    msg = '%s does not exist.' % team_data['name']
                    self.abort(409, msg)
            else:
                msg = 'No team name was given.'
                self.abort(409, msg)

class MainPage(webapp2.RequestHandler):

    def get(self):
        self.response.write("Welcome to my final project you filthy animal")

allowed_methods = webapp2.WSGIApplication.allowed_methods
new_allowed_methods = allowed_methods.union(('PATCH',))
webapp2.WSGIApplication.allowed_methods = new_allowed_methods
app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/heroes', HeroHandler),
    ('/heroes/(.*)/solo', SoloHandler),
    ('/heroes/(.*)/team_up', TeamUpHandler),
    ('/heroes/(.*)', HeroHandler),
    ('/teams', TeamHandler),
    ('/teams/(.*)', TeamHandler)
], debug=True)
