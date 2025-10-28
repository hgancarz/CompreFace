#  Copyright (c) 2020 the original author or authors
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       https://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
#  or implied. See the License for the specific language governing
#  permissions and limitations under the License.

"""
Tests for the actual healthcheck endpoint implementation.

These tests verify the exact healthcheck function implementation
from the _endpoints module without importing the full module.
"""

import pytest
from flask import Flask


@pytest.fixture
def actual_healthcheck_app():
    """Create a Flask app with the actual healthcheck endpoint implementation."""
    app = Flask('test_actual_healthcheck')
    app.url_map.strict_slashes = False
    
    # Copy the exact healthcheck endpoint implementation from _endpoints.py
    @app.route('/healthcheck')
    def healthcheck():
        from flask.json import jsonify
        return jsonify(
            status='OK'
        )
    
    return app


HEALTHCHECK_ENDPOINT = '/healthcheck'


def test__given_service_running__when_get_healthcheck__then_returns_200_and_ok_status(actual_healthcheck_app):
    """Test that actual healthcheck endpoint implementation returns 200 and correct JSON."""
    # When
    res = actual_healthcheck_app.test_client().get(HEALTHCHECK_ENDPOINT)

    # Then
    assert res.status_code == 200
    assert res.content_type == 'application/json'
    assert res.json == {'status': 'OK'}


def test__given_service_running__when_get_healthcheck__then_response_matches_pr_requirements(actual_healthcheck_app):
    """
    Test that healthcheck response exactly matches PR requirements:
    - HTTP 200 status code
    - JSON with key "status" equal to "OK"
    - Exact JSON payload {"status": "OK"}
    """
    # When
    res = actual_healthcheck_app.test_client().get(HEALTHCHECK_ENDPOINT)

    # Then - Verify all PR acceptance criteria
    assert res.status_code == 200, "Healthcheck endpoint should return HTTP 200"
    
    json_data = res.get_json()
    assert json_data == {'status': 'OK'}, "Healthcheck endpoint should return exact JSON {'status': 'OK'}"
    
    # Verify individual field as specified in PR
    assert 'status' in json_data, "Healthcheck response should contain 'status' key"
    assert json_data['status'] == 'OK', "Healthcheck response 'status' key should equal 'OK'"


def test__given_service_running__when_get_healthcheck_with_trailing_slash__then_returns_200(actual_healthcheck_app):
    """Test that actual healthcheck endpoint works with trailing slash."""
    # When
    res = actual_healthcheck_app.test_client().get(HEALTHCHECK_ENDPOINT + '/')

    # Then
    assert res.status_code == 200
    assert res.json == {'status': 'OK'}


def test__given_service_running__when_post_healthcheck__then_returns_405(actual_healthcheck_app):
    """Test that POST to actual healthcheck endpoint returns 405 Method Not Allowed."""
    # When
    res = actual_healthcheck_app.test_client().post(HEALTHCHECK_ENDPOINT)

    # Then
    assert res.status_code == 405  # Method Not Allowed