-- WWF Database Initialization Script

-- Create tables if they don't exist (for PostgreSQL)

-- Create ENUM types
DO $$ BEGIN
    CREATE TYPE conservation_status AS ENUM (
        'EXTINCT',
        'EXTINCT_IN_WILD',
        'CRITICALLY_ENDANGERED', 
        'ENDANGERED',
        'VULNERABLE',
        'NEAR_THREATENED',
        'LEAST_CONCERN',
        'DATA_DEFICIENT',
        'NOT_EVALUATED'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE project_status AS ENUM (
        'PLANNING',
        'ACTIVE',
        'ON_HOLD',
        'COMPLETED',
        'CANCELLED'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE donation_status AS ENUM (
        'PENDING',
        'PROCESSING',
        'COMPLETED',
        'FAILED',
        'REFUNDED'
    );
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Insert sample data for demonstration

-- Sample Wildlife Species
INSERT INTO wildlife_species (name, scientific_name, conservation_status, description, habitat, population_estimate, created_at) VALUES
('Giant Panda', 'Ailuropoda melanoleuca', 'VULNERABLE', 'Large bear native to south central China', 'Bamboo forests', 1864, NOW()),
('Snow Leopard', 'Panthera uncia', 'VULNERABLE', 'Large cat native to the mountain ranges of Central and South Asia', 'Alpine and subalpine zones', 4000, NOW()),
('African Elephant', 'Loxodonta africana', 'ENDANGERED', 'Largest living terrestrial animal', 'Savannas, forests, deserts and marshes', 415000, NOW()),
('Sumatran Tiger', 'Panthera tigris sumatrae', 'CRITICALLY_ENDANGERED', 'Tiger subspecies native to the Indonesian island of Sumatra', 'Tropical forests', 400, NOW()),
('Blue Whale', 'Balaenoptera musculus', 'ENDANGERED', 'Marine mammal and the largest animal known to have ever existed', 'Oceans worldwide', 25000, NOW())
ON CONFLICT (scientific_name) DO NOTHING;

-- Sample Conservation Projects
INSERT INTO conservation_projects (name, description, start_date, end_date, budget, funds_raised, status, location, species_id, created_at) VALUES
('Panda Habitat Restoration', 'Restoring bamboo forests for giant pandas in Sichuan Province', '2024-01-01', '2026-12-31', 500000.00, 150000.00, 'ACTIVE', 'Sichuan, China', 1, NOW()),
('Snow Leopard Protection Initiative', 'Community-based conservation program for snow leopards', '2024-03-01', '2027-02-28', 750000.00, 200000.00, 'ACTIVE', 'Himalayas', 2, NOW()),
('African Elephant Anti-Poaching', 'Anti-poaching efforts and community education', '2024-02-01', '2025-01-31', 1000000.00, 800000.00, 'ACTIVE', 'Kenya', 3, NOW()),
('Sumatran Tiger Sanctuary', 'Establishing protected areas for critically endangered Sumatran tigers', '2024-06-01', '2029-05-31', 2000000.00, 300000.00, 'PLANNING', 'Sumatra, Indonesia', 4, NOW()),
('Blue Whale Research Program', 'Research and monitoring of blue whale populations', '2024-04-01', '2026-03-31', 600000.00, 100000.00, 'ACTIVE', 'Pacific Ocean', 5, NOW())
ON CONFLICT DO NOTHING;

-- Sample Donations
INSERT INTO donations (amount, donor_name, donor_email, message, project_id, status, transaction_id, created_at, processed_at) VALUES
(100.00, 'John Smith', 'john.smith@email.com', 'Save the pandas!', 1, 'COMPLETED', 'TXN-001', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(250.00, 'Sarah Johnson', 'sarah.j@email.com', 'For the snow leopards', 2, 'COMPLETED', 'TXN-002', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
(500.00, 'Michael Brown', 'mbrown@email.com', 'Stop elephant poaching', 3, 'COMPLETED', 'TXN-003', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
(1000.00, 'Emma Wilson', 'emma.wilson@email.com', 'Critical support for tigers', 4, 'COMPLETED', 'TXN-004', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
(75.00, 'David Lee', 'david.lee@email.com', 'Blue whale conservation', 5, 'COMPLETED', 'TXN-005', NOW(), NOW()),
(200.00, 'Anonymous Donor', 'anon@email.com', 'General wildlife support', NULL, 'PENDING', NULL, NOW(), NULL)
ON CONFLICT DO NOTHING;