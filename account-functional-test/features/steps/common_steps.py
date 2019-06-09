from assertpy import assert_that
from behave import then, given
from requests import api


@given('user prepares an API request')
def step_impl(context):
    context.request = {'body': {}}


@given('request body has {field} {value}')
def step_impl(context, field, value):
    if value != 'NULL':
        context.request['body'][field] = value


@when('user submits a {http_method} request to {resource_path}')
def step_impl(context, http_method, resource_path):
    print(context.request['body'])
    endpoint = f"{context.config['base_url']}{resource_path}"
    context.response = api.request(http_method, endpoint, json=context.request['body'])


@then('response status is {http_status}')
def step_impl(context, http_status):
    assert_that(str(context.response.status_code)).is_equal_to(http_status)


@then('response body contains {field}')
def step_impl(context, field):
    response_body = context.response.json()
    assert_that(response_body).contains(field)


@then('response body has {field} {value}')
def step_impl(context, field, value):
    response_body = context.response.json()
    assert_that(response_body).contains(field)
    assert_that(str(response_body[field])).is_equal_to(value)
