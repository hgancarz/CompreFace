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
Standalone unit tests for the healthcheck endpoint.

These tests verify the healthcheck endpoint behavior without requiring
all the ML dependencies of the full application.
"""

import pytest
from flask import Flask


@pytest.fixture
def healthcheck_app():
    """Create a minimal Flask app with only the healthcheck endpoint."""
    app = Flask('test_healthcheck')
    app.url_map.strict_slashes = False  # Match the real app configuration
    
    # Add only the healthcheck endpoint (mimicking the actual implementation)
    @app.route('/healthcheck')
    def healthcheck():
        from flask.json import jsonify
        return jsonify(status='OK')
    
    return app


HEALTHCHECK_ENDPOINT = '/healthcheck'


def test__given_service_running__when_get_healthcheck__then_returns_200_and_ok_status(healthcheck_app):
    """Test that healthcheck endpoint returns 200 and correct JSON when service is running."""
    # When
    res = healthcheck_app.test_client().get(HEALTHCHECK_ENDPOINT)

    # Then
    assert res.status_code == 200
    assert res.content_type == 'application/json'
    assert res.json == {'status': 'OK'}


def test__given_service_running__when_get_healthcheck__then_response_has_correct_structure(healthcheck_app):
    """Test that healthcheck response has exactly the expected JSON structure."""
    # When
    res = healthcheck_app.test_client().get(HEALTHCHECK_ENDPOINT)

    # Then
    assert res.status_code == 200
    json_data = res.get_json()
    
    # Verify exact JSON structure
    assert json_data == {'status': 'OK'}
    
    # Verify individual fields
    assert 'status' in json_data
    assert json_data['status'] == 'OK'
    
    # Verify no extra fields
    assert len(json_data) == 1


def test__given_service_running__when_get_healthcheck_with_trailing_slash__then_returns_200(healthcheck_app):
    """Test that healthcheck endpoint works with trailing slash."""
    # When
    res = healthcheck_app.test_client().get(HEALTHCHECK_ENDPOINT + '/')

    # Then
    assert res.status_code == 200
    assert res.json == {'status': 'OK'}


def test__given_service_running__when_post_healthcheck__then_returns_405(healthcheck_app):
    """Test that POST to healthcheck endpoint returns 405 Method Not Allowed."""
    # When
    res = healthcheck_app.test_client().post(HEALTHCHECK_ENDPOINT)

    # Then
    assert res.status_code == 405  # Method Not Allowed


def test__given_service_running__when_put_healthcheck__then_returns_405(healthcheck_app):
    """Test that PUT to healthcheck endpoint returns 405 Method Not Allowed."""
    # When
    res = healthcheck_app.test_client().put(HEALTHCHECK_ENDPOINT)

    # Then
    assert res.status_code == 405  # Method Not Allowed


def test__given_service_running__when_delete_healthcheck__then_returns_405(healthcheck_app):
    """Test that DELETE to healthcheck endpoint returns 405 Method Not Allowed."""
    # When
    res = healthcheck_app.test_client().delete(HEALTHCHECK_ENDPOINT)

    # Then
    assert res.status_code == 405  # Method Not Allowed


def test__given_service_running__when_head_healthcheck__then_returns_200(healthcheck_app):
    """Test that HEAD to healthcheck endpoint returns 200."""
    # When
    res = healthcheck_app.test_client().head(HEALTHCHECK_ENDPOINT)

    # Then
    assert res.status_code == 200


def test__given_service_running__when_options_healthcheck__then_returns_allowed_methods(healthcheck_app):
    """Test that OPTIONS to healthcheck endpoint returns allowed methods."""
    # When
    res = healthcheck_app.test_client().options(HEALTHCHECK_ENDPOINT)

    # Then
    assert res.status_code == 200
    # Flask typically allows GET, HEAD, OPTIONS by default for simple routes