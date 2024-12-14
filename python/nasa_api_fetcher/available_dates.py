import requests
from datetime import datetime, timedelta

def get_epic_dates(api_key="DEMO_KEY"):
    url = f"https://api.nasa.gov/EPIC/api/natural/all?api_key={api_key}"
    response = requests.get(url)
    if response.status_code == 200:
        data = response.json()
        # Extract available dates and format as datetime objects
        available_dates = {datetime.strptime(item['date'], '%Y-%m-%d') for item in data}
        return available_dates
    else:
        raise Exception(f"Error fetching data: {response.status_code}")

def find_missing_dates(api_key="DEMO_KEY", start_date="2015-06-13"):
    start_date = datetime.strptime(start_date, '%Y-%m-%d')
    end_date = datetime.now()
    
    # Generate the full range of dates
    all_dates = {start_date + timedelta(days=i) for i in range((end_date - start_date).days + 1)}
    
    # Get the dates from the API
    available_dates = get_epic_dates(api_key)
    
    # Find missing dates
    missing_dates = sorted(all_dates - available_dates)
    return missing_dates

def find_largest_contiguous_block(api_key="DEMO_KEY", start_date="2015-06-13"):
    start_date = datetime.strptime(start_date, '%Y-%m-%d')
    end_date = datetime.now()
    
    # Get the available dates from the API
    available_dates = sorted(get_epic_dates(api_key))
    
    # Find the largest contiguous block
    longest_start = None
    longest_end = None
    longest_length = 0
    
    current_start = available_dates[0]
    current_length = 1
    
    for i in range(1, len(available_dates)):
        if available_dates[i] - available_dates[i - 1] == timedelta(days=1):
            current_length += 1
        else:
            if current_length > longest_length:
                longest_start = current_start
                longest_end = available_dates[i - 1]
                longest_length = current_length
            current_start = available_dates[i]
            current_length = 1
    
    # Final check for the last sequence
    if current_length > longest_length:
        longest_start = current_start
        longest_end = available_dates[-1]
        longest_length = current_length
    
    return longest_start, longest_end, longest_length

if __name__ == "__main__":
    longest_block = find_largest_contiguous_block()
    if longest_block:
        start, end, length = longest_block
        print(f"Largest contiguous block of available dates is from {start.strftime('%Y-%m-%d')} to {end.strftime('%Y-%m-%d')} ({length} days).")
    else:
        print("No contiguous block found!")